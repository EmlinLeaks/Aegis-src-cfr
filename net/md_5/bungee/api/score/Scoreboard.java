/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.score;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Position;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Team;

public class Scoreboard {
    private String name;
    private Position position;
    private final Map<String, Objective> objectives = new HashMap<String, Objective>();
    private final Map<String, Score> scores = new HashMap<String, Score>();
    private final Map<String, Team> teams = new HashMap<String, Team>();

    public Collection<Objective> getObjectives() {
        return Collections.unmodifiableCollection(this.objectives.values());
    }

    public Collection<Score> getScores() {
        return Collections.unmodifiableCollection(this.scores.values());
    }

    public Collection<Team> getTeams() {
        return Collections.unmodifiableCollection(this.teams.values());
    }

    public void addObjective(Objective objective) {
        Preconditions.checkNotNull(objective, (Object)"objective");
        Preconditions.checkArgument((boolean)(!this.objectives.containsKey((Object)objective.getName())), (String)"Objective %s already exists in this scoreboard", (Object)objective.getName());
        this.objectives.put((String)objective.getName(), (Objective)objective);
    }

    public void addScore(Score score) {
        Preconditions.checkNotNull(score, (Object)"score");
        this.scores.put((String)score.getItemName(), (Score)score);
    }

    public Score getScore(String name) {
        return this.scores.get((Object)name);
    }

    public void addTeam(Team team) {
        Preconditions.checkNotNull(team, (Object)"team");
        Preconditions.checkArgument((boolean)(!this.teams.containsKey((Object)team.getName())), (String)"Team %s already exists in this scoreboard", (Object)team.getName());
        this.teams.put((String)team.getName(), (Team)team);
    }

    public Team getTeam(String name) {
        return this.teams.get((Object)name);
    }

    public Objective getObjective(String name) {
        return this.objectives.get((Object)name);
    }

    public void removeObjective(String objectiveName) {
        this.objectives.remove((Object)objectiveName);
    }

    public void removeScore(String scoreName) {
        this.scores.remove((Object)scoreName);
    }

    public void removeTeam(String teamName) {
        this.teams.remove((Object)teamName);
    }

    public void clear() {
        this.name = null;
        this.position = null;
        this.objectives.clear();
        this.scores.clear();
        this.teams.clear();
    }

    public String getName() {
        return this.name;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Scoreboard)) {
            return false;
        }
        Scoreboard other = (Scoreboard)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        Position this$position = this.getPosition();
        Position other$position = other.getPosition();
        if (this$position == null ? other$position != null : !((Object)((Object)this$position)).equals((Object)((Object)other$position))) {
            return false;
        }
        Collection<Objective> this$objectives = this.getObjectives();
        Collection<Objective> other$objectives = other.getObjectives();
        if (this$objectives == null ? other$objectives != null : !((Object)this$objectives).equals(other$objectives)) {
            return false;
        }
        Collection<Score> this$scores = this.getScores();
        Collection<Score> other$scores = other.getScores();
        if (this$scores == null ? other$scores != null : !((Object)this$scores).equals(other$scores)) {
            return false;
        }
        Collection<Team> this$teams = this.getTeams();
        Collection<Team> other$teams = other.getTeams();
        if (this$teams == null) {
            if (other$teams == null) return true;
            return false;
        }
        if (((Object)this$teams).equals(other$teams)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Scoreboard;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Position $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : ((Object)((Object)$position)).hashCode());
        Collection<Objective> $objectives = this.getObjectives();
        result = result * 59 + ($objectives == null ? 43 : ((Object)$objectives).hashCode());
        Collection<Score> $scores = this.getScores();
        result = result * 59 + ($scores == null ? 43 : ((Object)$scores).hashCode());
        Collection<Team> $teams = this.getTeams();
        return result * 59 + ($teams == null ? 43 : ((Object)$teams).hashCode());
    }

    public String toString() {
        return "Scoreboard(name=" + this.getName() + ", position=" + (Object)((Object)this.getPosition()) + ", objectives=" + this.getObjectives() + ", scores=" + this.getScores() + ", teams=" + this.getTeams() + ")";
    }
}

