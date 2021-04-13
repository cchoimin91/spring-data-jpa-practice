package study.datajap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;
import study.datajap.entity.Team;
import study.datajap.repository.MemberRepository;
import study.datajap.repository.TeamRepository;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberRepository memberRepository;

    private final TeamRepository teamRepository;

    @PostConstruct
    public void init(){
        log.info("데이터 입력중...");
        for(int i=0 ; i<30 ; i++){
            teamRepository.save(new Team("팀"+i));
            memberRepository.save(new Member("민"+i, i));
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
        Page<Member> pageMembers =  memberRepository.findAll(pageable);
        Page<MemberDto> map = pageMembers
                //.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
                .map(MemberDto::new);

        return map;
    }


}
