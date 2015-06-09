package org.muscat.kermit.log.tntfl;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SubmittedGame extends Game {

  private float _skillChange;
  private Player _skillChangeDirection;
  private Date _dateTime;

  private int _redRankChange;
  private int _blueRankChange;

  private int _redNewRank;
  private int _blueNewRank;

  public float getSkillChange() {
    return _skillChange;
  }
  public void setSkillChange(final float skillChange) {
    _skillChange = skillChange;
  }
  public Player getSkillChangeDirection() {
    return _skillChangeDirection;
  }
  public void setSkillChangeDirection(final Player skillChangeDirection) {
    _skillChangeDirection = skillChangeDirection;
  }
  public Date getDateTime() {
    return _dateTime;
  }
  public void setDateTime(final Date dateTime) {
    _dateTime = dateTime;
  }

  public int getRankChange(final Player player) {
    if (player == Player.BLUE) {
      return _blueRankChange;
    }
    return _redRankChange;
  }


  public int getNewRank(final Player player) {
    if (player == Player.BLUE) {
      return _blueNewRank;
    }
    return _redNewRank;
  }


  // For hashCode() and equals() let's just make the assumption that no two games are played at the exact same time
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_dateTime == null) ? 0 : _dateTime.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SubmittedGame)) {
      return false;
    }
    final SubmittedGame other = (SubmittedGame) obj;
    if (_dateTime == null) {
      if (other._dateTime != null) {
        return false;
      }
    }
    else if (!_dateTime.equals(other._dateTime)) {
      return false;
    }
    return true;
  }

  public static SubmittedGame fromJsonString(final String json) {

    final JsonParser p = new JsonParser();
    final JsonObject o = p.parse(json).getAsJsonObject();

    return fromJsonObject(o);
  }

  protected static SubmittedGame fromJsonObject(final JsonObject o) {
    final SubmittedGame g = new SubmittedGame();
    final JsonObject redPlayer = o.get("red").getAsJsonObject();
    g.setRedPlayer(redPlayer.get("name").getAsString());
    g.setRedScore(redPlayer.get("score").getAsInt());

    final JsonObject bluePlayer = o.get("blue").getAsJsonObject();
    g.setBluePlayer(bluePlayer.get("name").getAsString());
    g.setBlueScore(bluePlayer.get("score").getAsInt());

    final float redSkill = redPlayer.get("skillChange").getAsFloat();
    final float blueSkill = bluePlayer.get("skillChange").getAsFloat();
    if (redSkill > 0) {
      g.setSkillChangeDirection(Player.RED);
      g.setSkillChange(redSkill);
    }
    else if (blueSkill > 0) {
      g.setSkillChangeDirection(Player.BLUE);
      g.setSkillChange(blueSkill);
    }

    if (redPlayer.has("rankChange")) {
      g._redRankChange = redPlayer.get("rankChange").getAsInt();
      g._blueRankChange = bluePlayer.get("rankChange").getAsInt();
      g._redNewRank = redPlayer.get("newRank").getAsInt();
      g._blueNewRank = bluePlayer.get("newRank").getAsInt();
    }


    if (o.has("date")) {
      g.setDateTime(new Date(o.get("date").getAsLong() * 1000));
    }
    else {
      g.setDateTime(new Date());
    }


    return g;
  }

  public static class SubmittedGameDeserializer implements JsonDeserializer<SubmittedGame> {
    @Override
    public SubmittedGame deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
      return SubmittedGame.fromJsonObject(json.getAsJsonObject());
    }

  }
}
