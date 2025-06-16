package com.example.taskflow.domain.comment.entity;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments")
@SQLDelete(sql = "UPDATE comments SET activated = 0 where id = ?")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String detail;

    @Column(nullable = false)
    private String userName;

    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task task;

    public Comment(Task task,String detail){
        this.task = task;
        this.detail = detail;
    }

}
