package sprint.server.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private long id;

    private String name;

    private String email;

    private float height;
    private float weight;


    private int mainGroupId;
    private int tierId;

    private String picture;


}