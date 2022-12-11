package com.bbs.member.service.impl;

import com.bbs.member.dto.MemberDto;
import com.bbs.member.model.Member;
import com.bbs.member.repository.MemberRepository;
import com.bbs.member.service.MemberService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberJpaServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberJpaServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean checkId(String memberId) {
        return memberRepository.findById(memberId).isPresent();
    }

    @Override
    public boolean checkPw(String memberId, String memberPw) throws Exception {
        if (memberPw != null) {
            return memberPw.equals(memberRepository.findById(memberId).orElseThrow(Exception::new).getMemberPw());
        }
        return false;
    }

    @Override
    public String login(HttpSession session, MemberDto memberDto) throws Exception {
        if ("".equals(memberDto.getMemberId().trim())) {
            return "redirect:/login?error=1";
        }

        if (!this.checkId(memberDto.getMemberId())) {
            return "redirect:/login?error=2";
        }

        if (!this.checkPw(memberDto.getMemberId(), memberDto.getMemberPw())) {
            return "redirect:/login?error=3";
        }

        session.setAttribute("member", memberRepository.findById(memberDto.getMemberId()));

        return "redirect:/boardList";
    }

    @Override
    public void logout(HttpSession session) {
        session.removeAttribute("member");
    }

    @Override
    public void register(MemberDto memberDto) {
        memberRepository.save(memberDto.toEntity());
    }

    @Override
    public MemberDto getMemberInfo(HttpSession session) {
        return null;
    }

    @Override
    public void editMemberInfo(MemberDto memberDto) throws Exception {
        Member member = memberRepository.findById(memberDto.getMemberId()).orElseThrow(Exception::new);
        member.update(member.getMemberId(),
                member.getMemberEmail(),
                member.getMemberPw(),
                member.getRegisterDate(),
                member.getUserLevel());
    }

    @Override
    public void deleteMemberInfo(String memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    public List<MemberDto> selectAllMember() {
        return memberRepository.findAll().stream()
                .map(Member::toDto)
                .collect(Collectors.toList());
    }
}
