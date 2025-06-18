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
@SQLDelete(sql = "UPDATE comments SET isDeleted = true where id = ?")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String comment;

    private int tasksId;

    @ManyToOne
    @JoinColumn(name = "task_Id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    public Comment(int tasksId,User user,String comment){
        //this.task = task;
        this.user = user;
        this.tasksId = tasksId;
        this.comment = comment;
    }

}
