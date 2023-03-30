package javachess.javachessfx.window.util;

public class GameTime
{
    private final int initialPlayTimeMinutes;
    private final int initialPlayTimeSeconds;
    private int whitePlayTimeMinutes;
    private int whitePlayTimeSeconds;
    private int blackPlayTimeSeconds;
    private int blackPlayTimeMinutes;

    public GameTime()
    {
        this.initialPlayTimeSeconds = 0;
        this.initialPlayTimeMinutes = 0;
    }
    public GameTime(int initialPlayTimeMinutes, int initialPlayTimeSeconds)
    {
        this.initialPlayTimeMinutes = initialPlayTimeMinutes;
        this.initialPlayTimeSeconds = initialPlayTimeSeconds;
        this.whitePlayTimeMinutes = this.initialPlayTimeMinutes;
        this.whitePlayTimeSeconds = this.initialPlayTimeSeconds;
        this.blackPlayTimeSeconds = this.initialPlayTimeSeconds;
        this.blackPlayTimeMinutes = this.initialPlayTimeMinutes;
    }

    public int getInitialPlayTimeMinutes()
    {
        return initialPlayTimeMinutes;
    }

    public int getInitialPlayTimeSeconds()
    {
        return initialPlayTimeSeconds;
    }

    public int getWhitePlayTimeMinutes()
    {
        return whitePlayTimeMinutes;
    }

    public void setWhitePlayTimeMinutes(int whitePlayTimeMinutes)
    {
        this.whitePlayTimeMinutes = whitePlayTimeMinutes;
    }

    public int getWhitePlayTimeSeconds()
    {
        return whitePlayTimeSeconds;
    }

    public void setWhitePlayTimeSeconds(int whitePlayTimeSeconds)
    {
        this.whitePlayTimeSeconds = whitePlayTimeSeconds;
    }

    public int getBlackPlayTimeSeconds()
    {
        return blackPlayTimeSeconds;
    }

    public void setBlackPlayTimeSeconds(int blackPlayTimeSeconds)
    {
        this.blackPlayTimeSeconds = blackPlayTimeSeconds;
    }

    public int getBlackPlayTimeMinutes()
    {
        return blackPlayTimeMinutes;
    }

    public void setBlackPlayTimeMinutes(int blackPlayTimeMinutes)
    {
        this.blackPlayTimeMinutes = blackPlayTimeMinutes;
    }
}
