package com.soft1851.contentcenter.controller;

import com.soft1851.contentcenter.domain.dto.ShareAuditDTO;
import com.soft1851.contentcenter.domain.entity.Share;
import com.soft1851.contentcenter.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shares")
@Api(tags = "管理员接口", value = "管理员接口")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {

    private final ShareService shareService;

    /**
     * 审核投稿（异步）
     * @param id
     * @param auditDto
     * @return
     */
    @PutMapping(value = "/audit/{id}")
    @ApiOperation(value = "管理员审核（异步）",notes = "管理员审核（异步）")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDto) {
        // 此处需要认证授权
        return this.shareService.auditById(id, auditDto);
    }

    /**
     * 审核投稿（同步）
     * @param id
     * @param auditDto
     * @return
     */
    @PutMapping(value = "/audit/syn/{id}")
    @ApiOperation(value = "管理员审核（同步）",notes = "管理员审核（同步）")
    public Share auditByIdSyn(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDto) {
        return this.shareService.auditByIdSyn(id, auditDto);
    }

}