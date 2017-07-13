package net.redstoneore.legacyfactions.scoreboards;

import com.google.common.base.Splitter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.*;

public class BufferedObjective {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

    private static final Method addEntryMethod;
    private static final int MAX_LINE_LENGTH;

    static {
        // Check for long line support.
        // We require use of Spigot's `addEntry(String)` method on
        // Teams, as adding OfflinePlayers to a team is far too slow.

        Method addEntryMethodLookup = null;
        try {
            addEntryMethodLookup = Team.class.getMethod("addEntry", String.class);
        } catch (NoSuchMethodException ignored) {
        	
        }

        addEntryMethod = addEntryMethodLookup;

        if (addEntryMethod != null) {
            MAX_LINE_LENGTH = 48;
        } else {
            MAX_LINE_LENGTH = 16;
        }
    }
    
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

    public BufferedObjective(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.baseName = this.createBaseName();

        this.current = scoreboard.registerNewObjective(getNextObjectiveName(), "dummy");
    }
    
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

    private final Scoreboard scoreboard;
    private final String baseName;

    private Objective current;
    private List<Team> currentTeams = new ArrayList<>();
    private String title;
    private DisplaySlot displaySlot;

    private int objPtr;
    private int teamPtr;
    private boolean requiresUpdate = false;

    private final Map<Integer, String> contents = new HashMap<>();

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    private String createBaseName() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < 14) {
            builder.append(Integer.toHexString(random.nextInt()));
        }
        return builder.toString().substring(0, 14);
    }

    public void setTitle(String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            this.requiresUpdate = true;
        }
    }

    public void setDisplaySlot(DisplaySlot slot) {
        this.displaySlot = slot;
        this.current.setDisplaySlot(slot);
    }

    public void setAllLines(List<String> lines) {
        if (lines.size() != this.contents.size()) {
        	this.contents.clear();
        }
        for (int i = 0; i < lines.size(); i++) {
        	this.setLine(lines.size() - i, lines.get(i));
        }
    }

    public void setLine(int lineNumber, String content) {
        if (content.length() > MAX_LINE_LENGTH) {
            content = content.substring(0, MAX_LINE_LENGTH);
        }
        content = ChatColor.translateAlternateColorCodes('&', content);

        if (this.contents.get(lineNumber) == null || !this.contents.get(lineNumber).equals(content)) {
        	this.contents.put(lineNumber, content);
            this.requiresUpdate = true;
        }
    }

    // Hides the objective from the display slot until flip() is called
    public void hide() {
        if (this.displaySlot != null) {
        	this.scoreboard.clearSlot(this.displaySlot);
        }
    }

    public void flip() {
        if (!this.requiresUpdate) return;
        
        this.requiresUpdate = false;

        Objective buffer = this.scoreboard.registerNewObjective(this.getNextObjectiveName(), "dummy");
        buffer.setDisplayName(this.title);

        List<Team> bufferTeams = new ArrayList<>();

        int counter = 0;

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            if (entry.getValue().length() <= 16) {
                Team team = scoreboard.registerNewTeam(getNextTeamName());
                bufferTeams.add(team);

                Iterator<String> split = Splitter.fixedLength(16).split(entry.getValue()).iterator();

                team.setPrefix(split.next());
                String name;

                if (split.hasNext()) {
                    name = split.next();
                } else {
                    counter++;
                    name = String.join("", Collections.nCopies(counter, "§r"));
                }

                if (split.hasNext()) { // We only guarantee two splits
                    team.setSuffix(split.next());
                }

                try {
                    addEntryMethod.invoke(team, name);
                } catch (ReflectiveOperationException ignored) {
                }
                buffer.getScore(name).setScore(entry.getKey());
            } else {
                buffer.getScore(entry.getValue()).setScore(entry.getKey());
            }
        }

        if (displaySlot != null) {
            buffer.setDisplaySlot(displaySlot);
        }

        // Unregister _ALL_ the old things
        current.unregister();

        Iterator<Team> it = currentTeams.iterator();
        while (it.hasNext()) {
            it.next().unregister();
            it.remove();
        }

        current = buffer;
        currentTeams = bufferTeams;
    }

    private String getNextObjectiveName() {
        return baseName + "_" + ((objPtr++) % 2);
    }

    private String getNextTeamName() {
        return baseName.substring(0, 10) + "_" + ((teamPtr++) % 999999);
    }
    
}
