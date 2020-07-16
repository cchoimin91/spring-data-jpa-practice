package study.datajap.dto;

import lombok.Data;
import study.datajap.entity.Member;

@Data
public class MemberDto {

    private long id;
    private String username;
    private String teamName;

    public MemberDto(long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member){
        this.id = member.getId();
        this.getUsername();
    }

}
