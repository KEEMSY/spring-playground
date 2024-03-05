package com.example.springskeleton.steps;

import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.domain.member.Role;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MemberStep {

    public Address createAddress() {
        return new Address("city", "street", "zipcode");
    }

    public Member createMemberEntity(Address address, Set<Role> roles) {
        Member member = new Member();
        member.setName("memberName");
        member.setEmail("Test@email.com");
        member.setPassword("securePassword");
        member.setAddress(address);
        member.setRoles(roles);

        return member;
    }
}
