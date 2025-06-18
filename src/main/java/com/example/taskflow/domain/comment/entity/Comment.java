package com.example.taskflow.domain.comment.entity;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int tasksId;

    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Comment(int tasksId,User user,String content){
        this.user = user;
        this.tasksId = tasksId;
        this.content = content;
    }

}
