package com.justeattakeaway.codechallenge;

import com.mongodb.lang.Nullable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameRequest {

    @Nullable
    private String playerName;
    private String gameMode;
    private String number;

}
