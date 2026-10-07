package ai.tool;

import java.util.ArrayList;
import java.util.List;

public class AgentToolRegistry {

    private final List<AgentTool> tools =
            new ArrayList<>();

    public void register(AgentTool tool) {

        if (tool == null) {
            throw new IllegalArgumentException(
                    "Tool cannot be null"
            );
        }

        tools.add(tool);
    }

    public List<AgentTool> getTools() {

        return List.copyOf(tools);
    }

    public AgentTool getTool(String toolName) {

        for (AgentTool tool : tools) {

            if (tool.getName()
                    .equalsIgnoreCase(toolName)) {

                return tool;
            }
        }

        return null;
    }
}