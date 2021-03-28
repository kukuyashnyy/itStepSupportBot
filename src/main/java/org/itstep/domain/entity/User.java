package org.itstep.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private Integer userId;
    @NonNull
    private String userName;

    private String firstName;

    private String lastName;

    private boolean isUser = false;

    private boolean isAdmin = false;

    private boolean isMaster = false;

}
