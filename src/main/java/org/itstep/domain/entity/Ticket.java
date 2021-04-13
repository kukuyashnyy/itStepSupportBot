package org.itstep.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private Integer userId;

    @NonNull
    private Integer messageFromId;

    private Integer messageToId;

    @NonNull
    private Integer messageDate;

    private boolean opened = false;

    private boolean closed = false;
}
