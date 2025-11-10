package domain.backend.fileexport.controller;

import domain.backend.common.mq.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class FileExportController {
    private final MessageProducer messageProducer;

    @GetMapping("/{message}")
    public String send(@PathVariable String message) {
        messageProducer.sendMessage(message);
        return "done! " + message;
    }
}
