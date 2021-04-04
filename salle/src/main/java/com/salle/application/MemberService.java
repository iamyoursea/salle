package com.salle.application;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salle.domain.Login;
import com.salle.domain.Member;
import com.salle.exception.IncorrectPasswordException;
import com.salle.exception.UnregisteredMemberException;
import com.salle.mapper.MemberMapper;

@Transactional
@Service
public class MemberService implements MemberMapper {
	
    MemberMapper memberMapper;

    @Autowired
    public MemberService(MemberMapper memberMapper) {
    	this.memberMapper = memberMapper;
    }

    @Override
    public void insertMember(Member member) {
    	ConfigurablePasswordEncryptor encryptor = new ConfigurablePasswordEncryptor(); 
    	encryptor.setAlgorithm("MD5");
    	encryptor.setPlainDigest(true);
    	String rawPwd = member.getPassword();
    	String encryptedPwd = encryptor.encryptPassword(rawPwd);
    	member.setPassword(encryptedPwd);
        memberMapper.insertMember(member);
    }

    @Override
    public Member memberInfo(String email) {
        return memberMapper.memberInfo(email);
    }

    public Login loginMember(Login login) {
    	ConfigurablePasswordEncryptor encryptor = new ConfigurablePasswordEncryptor(); 
    	encryptor.setAlgorithm("MD5");
    	encryptor.setPlainDigest(true);
		Object memberInfo = ObjectUtils.defaultIfNull(memberMapper.memberInfo(login.getEmail()) , 
				null);
		Member memberInfoConvert = (Member) memberInfo;
		
        if (memberInfo == null) {
            throw new UnregisteredMemberException();
        } else {
        	boolean checkPwd = encryptor.checkPassword(login.getPassword(), memberInfoConvert.getPassword());
            if (!checkPwd)
                throw new IncorrectPasswordException();
        }
        
        login.setNickName(memberInfoConvert.getNickName()); 

        return login;
    }

}
