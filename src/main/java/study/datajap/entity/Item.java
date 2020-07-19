package study.datajap.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 새로운 엔티티를 판단하는 방법
 * 식별자가 객체일 경우 -> null 로 판단
 *          기본타입일 경우 -> 0 으로 판단
 *
 * 식별전략이 @Id만 사용할 경우, 이미 식별자값이 있는 상태이므로 save()호출함, 이경우 merget()를 호출해버림
 * merge는 DB를 호출해 값을 확인하기 때문에 비효율.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item implements Persistable<String> {

/*
    @Id
    @GeneratedValue // jpa안에서 persist하면 값이 들어감
    private Long id ;
*/

    @Id
    //@GeneratedValue // jpa안에서 persist하면 값이 들어감
    private String id ;

    @CreatedDate
    private LocalDateTime createdDate;  // 키를 문자로 사용할 경우

    public Item (String id){
        this.id=id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }

/**
     * simpleJpaRepository.class
     *
     * merge의 경우 DB에 값이 있을 것이라고 가정하고 작동함
     *
     *    @Transactional
     *    @Override
     *    public <S extends T> S save(S entity) {
     *
     * 		if (entityInformation.isNew(entity)) {
     * 			em.persist(entity);
     * 			return entity;
     *        } else {
     * 			return em.merge(entity);
     *        }
     *    }
     */

}
