package ua.karda4.sorcerer;

public class Skill {
	int id;
	String title;
	String description;
	int min_pers_level;
	int demand[];
	final int a_demand = 3;
	int param[][];
	int maxLevel;
	
	int gameBoxIndex;
	
	public Skill(String title, String descrip){
		this.title = title;
		this.description = descrip;
		demand = new int[a_demand];
		gameBoxIndex = -1;
	}
	
	static final int HERO_STRIKE = 0;
	static final int HERO_SHOT = 1;
	static final int HERO_DODGE = 2;
	static final int HERO_ACCELERATION = 3;
	static final int HERO_PRAYER = 4;
	static final int HERO_PROVOCATION = 5;
	static final int HERO_RAIN = 6;
	static final int HERO_BEER = 7;
	static final int HERO_HANDFUL_EARTH = 8;
	static final int HERO_FIG = 9;
	static final int HERO_GREED = 10;
	static final int HERO_THROW_STONE = 11;
	static final int HERO_KICK = 12;
	static final int HERO_EARTHQUAKE = 13;
	static final int HERO_POISON_EXPLOSION = 14;
	
	static final int MONSTER_STRIKE = 0;
	static final int MONSTER_BITE = 1;
	static final int MONSTER_BURP = 2;
	static final int MONSTER_ROOTS = 3;
	static final int MONSTER_SEA_WAVE = 4;
	static final int MONSTER_SONG = 5;
	static final int MONSTER_LAUGH = 6;
	static final int MONSTER_LIGHTING = 7;
	static final int MONSTER_FIREBALL = 8;
	static final int MONSTER_VAMPIRE = 9;
	static final int MONSTER_REGENARATION = 10;
	
	static final int SP_ID = 0;
	static final int SP_LEVEL = 1;
	static final int SP_MONEY = 2;
	static final int SP_CASH = 3;
	static final int SP_FIRE = 4;
	static final int SP_WATER = 5;
	static final int SP_AIR = 6;
	static final int SP_EARTH = 7;
	static final int SP_EXTRA_DAMAGE = 8;
	static final int SP_EXTRA_STEP = 9;
	static final int SP_EXTRA_HP = 10;
	static final int aSP = 11;
	
	public void initParam(int data[][]){
		maxLevel = data.length;
		int aa;
		param = new int[maxLevel][];
		for(int i = 0; i < maxLevel; i++){
			aa = data[i].length;
			param[i] = new int[aa];
			System.arraycopy(data[i], 0, param[i], 0, aa);
		}
	}
	
	public int getParam(int level, int type){
		return param[level][type];
	}
}
