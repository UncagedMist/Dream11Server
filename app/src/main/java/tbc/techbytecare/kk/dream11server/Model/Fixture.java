package tbc.techbytecare.kk.dream11server.Model;

public class Fixture {

    private String id,seriesName,timeLeft,firstOpponent,secondOpponent;

    public Fixture() {
    }

    public Fixture(String id, String seriesName, String timeLeft, String firstOpponent, String secondOpponent) {
        this.id = id;
        this.seriesName = seriesName;
        this.timeLeft = timeLeft;
        this.firstOpponent = firstOpponent;
        this.secondOpponent = secondOpponent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getFirstOpponent() {
        return firstOpponent;
    }

    public void setFirstOpponent(String firstOpponent) {
        this.firstOpponent = firstOpponent;
    }

    public String getSecondOpponent() {
        return secondOpponent;
    }

    public void setSecondOpponent(String secondOpponent) {
        this.secondOpponent = secondOpponent;
    }
}
