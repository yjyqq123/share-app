package com.soft1851.contentcenter.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.soft1851.contentcenter.dao.MidUserShareMapper;
import com.soft1851.contentcenter.dao.ShareMapper;
import com.soft1851.contentcenter.domain.dto.*;
import com.soft1851.contentcenter.domain.entity.MidUserShare;
import com.soft1851.contentcenter.domain.entity.Share;
import com.soft1851.contentcenter.domain.enums.AuditStatusEnum;
import com.soft1851.contentcenter.feignclient.UserCenterFeignClient;
import com.soft1851.contentcenter.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareServiceImpl implements ShareService {
    private final ShareMapper shareMapper;
    private final MidUserShareMapper midUserShareMapper;
    private final UserCenterFeignClient userCenterFeignClient;


    @Override
    public ShareDTO findById(Integer id) {
        // 获取分享实体
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 获得发布人id
        Integer userId = share.getUserId();

        // 1. 代码不可读
        // 2. 复杂的url难以维护：https://user-center/s?ie={ie}&f={f}&rsv_bp=1&rsv_idx=1&tn=baidu&wd=a&rsv_pq=c86459bd002cfbaa&rsv_t=edb19hb%2BvO%2BTySu8dtmbl%2F9dCK%2FIgdyUX%2BxuFYuE0G08aHH5FkeP3n3BXxw&rqlang=cn&rsv_enter=1&rsv_sug3=1&rsv_sug2=0&inputT=611&rsv_sug4=611
        // 3. 难以相应需求的变化，变化很没有幸福感
        // 4. 编程体验不统一
        UserDTO userDTO = this.userCenterFeignClient.findUserById(userId);
        ShareDTO shareDTO = new ShareDTO();
        shareDTO.setShare(share);
        // 属性的装配
//        BeanUtils.copyProperties(share, shareDto);
//        shareDto.setWxNickname(userDto.getWxNickname());
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    @Override
    public String getHello() {
        return this.userCenterFeignClient.getHello();
    }


    @Override
    public PageInfo<Share> query(String title, Integer pageNo, Integer pageSize, Integer userId) {
        //启动分页
        PageHelper.startPage(pageNo,pageSize);
        //构造查询实例
        Example example = new Example(Share.class);
        Example.Criteria criteria = example.createCriteria();
        //如标题关键字不空，则加上模糊查询条件，否则结果即所有数据
        if(StringUtil.isNotEmpty((title))){
            criteria.andLike("title","%"+title+"%");
        }
        //执行按条件查询
        List<Share> shares = this.shareMapper.selectByExample(example);
        //处理后的Shares数据列表
        List<Share> sharesDeal;
        //1，如果用户未登录，那么downloadUrl全部设为null
        if(userId == null){
            sharesDeal = shares.stream()
                    .peek(share -> {
                        share.setDownloadUrl(null);
                    })
                    .collect(Collectors.toList());
        }
        //2.如果用户登录了，那么查询一下mid_user_share,如果没有数据，那么这条share的downloadUrl也设为null
        //只有自己分享的资源才能直接看到下载链接，否则显示为“兑换”
        else {
            sharesDeal = shares.stream()
                    .peek(share -> {
                        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                                MidUserShare.builder()
                                        .userId(userId)
                                        .shareId(share.getId())
                                        .build()
                        );
                        if (midUserShare == null){
                            share.setDownloadUrl(null);
                        }
                    })
                    .collect(Collectors.toList());
        }
        return new PageInfo<>(sharesDeal);
    }

    @Override
    public int insertShare(ContributeDto contributeDto) {
        Share share1 = Share.builder()
                .userId(contributeDto.getUserId())
                .title(contributeDto.getTitle())
                .createTime(Date.valueOf(LocalDate.now()))
                .updateTime(Date.valueOf(LocalDate.now()))
                .isOriginal(contributeDto.getIsOriginal())
                .author(contributeDto.getAuthor())
                .cover(contributeDto.getCover())
                .summary(contributeDto.getSummary())
                .price(contributeDto.getPrice())
                .downloadUrl(contributeDto.getDownloadUrl())
                .buyCount(0)
                .showFlag(false)
                .auditStatus("NOT_YET")
                .reason("未审核")
                .build();
        return shareMapper.insert(share1);
    }

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public Share auditById(Integer id, ShareAuditDTO shareAuditDto) {
        // 1.查询share是否存在，不存在或者当前的audit_status != NOT_YET,那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法!该分享不存在!");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法!该分享已审核通过或审核不通过!");
        }

        // 2.审核资源，将状态码改为PASS或REJECT
        // 这个API主要流程是审核，所以不需要等积分更新的结果返回，可以将积分改为异步
        share.setAuditStatus(shareAuditDto.getAuditStatusEnum().toString());
        share.setReason(shareAuditDto.getReason());
        this.shareMapper.updateByPrimaryKey(share);

        // 3.如果是PASS，那么发送信息给rocketmq，让用户中心去消费，并为发布人添加积分
        if (AuditStatusEnum.PASS.equals(shareAuditDto.getAuditStatusEnum())) {
            this.rocketMQTemplate.convertAndSend(
                    "add-bonus",
                    UserAddBonusMsgDto.builder()
                            .userId(share.getUserId())
                            .bonus(50)
                            .build());
        }
        return share;
    }

    @Override
    public Share auditByIdSyn(Integer id, ShareAuditDTO shareAuditDto) {
        // 1.查询share是否存在，不存在或者当前的audit_status != NOT_YET,那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法!该分享不存在!");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法!该分享已审核通过或审核不通过!");
        }
        // 2.审核资源，将状态码改为PASS或REJECT
        share.setAuditStatus(shareAuditDto.getAuditStatusEnum().toString());
        share.setReason(shareAuditDto.getReason());
        this.shareMapper.updateByPrimaryKey(share);

        // 3.如果是PASS  改用同步的方法给用户增加积分
        UserAddBonusMsgDto userAddBonusMsgDto = UserAddBonusMsgDto.builder()
                .userId(share.getUserId())
                .bonus(50)
                .build();
        this.userCenterFeignClient.addBonusToUser(userAddBonusMsgDto);
        return share;
    }
}