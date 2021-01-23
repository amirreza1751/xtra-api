package com.xtra.api;

import com.xtra.api.model.Line;
import com.xtra.api.service.LineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
@DataJpaTest
public class LineOperations {
    @Autowired
    LineService lineService;

    @Test
    Line insertLineTest() {
        Line line = new Line();
        lineService.insert()
    }
}
