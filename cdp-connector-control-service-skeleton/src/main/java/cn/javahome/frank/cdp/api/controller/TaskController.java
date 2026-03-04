package cn.javahome.frank.cdp.api.controller;

import cn.javahome.frank.cdp.api.model.TaskMode;
import cn.javahome.frank.cdp.api.model.TaskResponse;
import cn.javahome.frank.cdp.api.model.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable String taskId) {
        return new TaskResponse(
                taskId,
                "conn-demo",
                TaskMode.REALTIME,
                TaskStatus.RUNNING,
                "wf-demo-01",
                "ins-demo-01",
                OffsetDateTime.now().minusMinutes(10),
                null,
                null
        );
    }

    @PostMapping("/{taskId}/pause")
    public TaskResponse pauseTask(@PathVariable String taskId) {
        return statusChanged(taskId, TaskStatus.PAUSED);
    }

    @PostMapping("/{taskId}/resume")
    public TaskResponse resumeTask(@PathVariable String taskId) {
        return statusChanged(taskId, TaskStatus.RUNNING);
    }

    @PostMapping("/{taskId}/rerun")
    public TaskResponse rerunTask(@PathVariable String taskId) {
        return statusChanged(taskId, TaskStatus.PUBLISHED);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        return ResponseEntity.noContent().build();
    }

    private TaskResponse statusChanged(String taskId, TaskStatus status) {
        return new TaskResponse(
                taskId,
                "conn-demo",
                TaskMode.BATCH,
                status,
                "wf-demo-01",
                "ins-demo-01",
                OffsetDateTime.now().minusMinutes(3),
                status == TaskStatus.PAUSED ? OffsetDateTime.now() : null,
                null
        );
    }
}
