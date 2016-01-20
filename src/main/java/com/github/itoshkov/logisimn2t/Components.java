package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Components extends Library {
    private final List<? extends Tool> tools = Collections.singletonList(new AddTool(HackDisplay.FACTORY));

    @Override
    public List<? extends Tool> getTools() {
        return tools;
    }

    @Override
    public String getDisplayName() {
        return "Nand2Tetris HACK components";
    }
}
