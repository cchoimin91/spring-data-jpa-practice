package study.datajap.entity;


import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;


// extends JpaBaseEntity 순수JPA사용시
@Getter
@MappedSuperclass // 멤버필만 공유, 실제로 상속은 아님.
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime regDate;

    private LocalDateTime updateDate;



    @PrePersist // persist전에 이벤트발생
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        regDate = now;
        updateDate = now;
    }

    @PreUpdate // update 되기전에 호출됨
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }

}
