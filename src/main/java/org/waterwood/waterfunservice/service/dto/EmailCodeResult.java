package org.waterwood.waterfunservice.service.dto;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class EmailCodeResult {
    private boolean sendSuccess;
    private final String email;
    private @Nullable String message;
    private @Nullable String responseRaw;

    protected String key;
}
