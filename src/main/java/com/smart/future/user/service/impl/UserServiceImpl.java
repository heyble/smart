package com.smart.future.user.service.impl;

import com.smart.future.common.constant.RoleCode;
import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.exception.ApplicationException;
import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.common.util.CollectionUtil;
import com.smart.future.dao.role.IRoleDao;
import com.smart.future.dao.user.IUserDao;
import com.smart.future.user.service.IUserService;
import com.smart.future.user.vo.RoleVO;
import com.smart.future.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao;
    @Autowired
    private IRoleDao roleDao;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    @Transactional
    public void addUser(UserVO userVO) throws ApplicationException {
        // 参数校验

        // 手机号校验
        UserVO userByPhone = userDao.findByPhoneId(userVO.getPhoneId());
        if (userByPhone != null) {
            throw new SmartApplicationException(SmartCode.UserError.ERROR_PARAM,"手机号已被注册");
        }
        if (userVO.getName() == null) {
            userVO.setName(generateDefaultUserName());
        }
        userVO.setPassword(encoder.encode(userVO.getPassword()));
        userVO.setAvailable(1);
        userVO.setCreationDate(new Date());
        userVO.setLastUpdatedDate(new Date());
        userDao.insertUser(userVO);
        // 关联角色
        createUserRoleRelationWithDefault(userVO);
    }

    public void createUserRoleRelationWithDefault(UserVO userVO) {
        List<RoleVO> roles = new ArrayList<>();
        if (CollectionUtil.isNullOrEmpty(userVO.getRoles())) {
            RoleVO defaultRole = getDefaultRole();
            roles.add(defaultRole);
        }else {
            roles.addAll(userVO.getRoles());
        }
        userDao.insertUserRoleRelation(userVO.getId(),roles.stream().map(RoleVO::getCode).collect(Collectors.toList()));
    }

    public RoleVO getDefaultRole() {
        RoleVO roleVO = new RoleVO();
        roleVO.setCode(RoleCode.GENERAL_USER.getCode());
        return roleVO;
    }

    private String generateDefaultUserName() {
        Long maxId = userDao.getMaxId();
        long timeMillis = System.currentTimeMillis();
        String tm = (timeMillis + "").substring(9);
        return "sfu-"+maxId+tm;
    }

    @Override
    public List<UserVO> findAll() {
        List<UserVO> all = userDao.findAll();
        return all;
    }

    @Override
    public UserVO updateUser(UserVO userVO) {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userVO.setLastUpdatedBy(Long.valueOf(principal.toString()));
        userVO.setLastUpdatedDate(new Date());
        userDao.updateUser(userVO);
        UserVO user = userDao.findById(userVO.getId());
        return user;
    }

    @Override
    public UserVO findUserByPhoneId(Long phoneId) {
        return userDao.findByPhoneId(phoneId);
    }

    @Override
    public UserVO currentUser() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long phoneId = Long.valueOf(principal.toString());
        return userDao.findByPhoneId(phoneId);
    }
}
