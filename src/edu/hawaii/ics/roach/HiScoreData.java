package edu.hawaii.ics.roach;


public class HiScoreData {

	public String	score  = "-1";
	public String	name   = "";
	public String	stage  = "lv1";
	public String	level  = "Normal";

	public HiScoreData() {
	}

	public HiScoreData(int score, int stage, int level) {
		String[] levelDesc = new String[] { "Easy", "Normal", "Hard" };

		this.score = String.valueOf(score);
		this.name  = "";
		this.stage = "lv" + stage;
		this.level = levelDesc[level];
	}

	public HiScoreData(String score, String name, String stage, String level) {
		this.score = score;
		this.name = name;
		this.stage = stage;
		this.level = level;
	}

}
