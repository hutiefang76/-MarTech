package cn.javahome.frank.cdp.tag.task.controller;

import cn.javahome.frank.cdp.common.tag.TagTaskPublishRequest;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishResponse;
import cn.javahome.frank.cdp.tag.task.service.TagTaskOrchestrator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag/task")
public class TagTaskController {

    private final TagTaskOrchestrator orchestrator;

    public TagTaskController(TagTaskOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/publish")
    public TagTaskPublishResponse publish(@Valid @RequestBody TagTaskPublishRequest request) {
        return orchestrator.publish(request);
    }

    @GetMapping("/{taskId}")
    public TagTaskPublishResponse get(@PathVariable String taskId) {
        return orchestrator.get(taskId);
    }

    @GetMapping("/list")
    public List<TagTaskPublishResponse> list() {
        return orchestrator.list();
    }
}
