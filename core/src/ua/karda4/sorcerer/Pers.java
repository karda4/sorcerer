package ua.karda4.sorcerer;

public class Pers {
	GameScreen game;
	public Pers(GameScreen gm, int kind){
		this.game = gm;
		this.kind = kind;
		oneLifeRegenTime = 180f;
		skillStep = new int[Skills.maxSkills[kind]];
		bAchievment = new boolean[GameScreen.aAchievments];
		reset();
	}
	
	public void startGame(){
		startManna();
		health = getHealthMax(true);
		bProvocation = false;
		skillUsingIndex = -1;
	}
	
	public void reset(){
		for(int i = GameScreen.aAchievments - 1; i >= 0; i--) bAchievment[i] = false;
		for(int i = maxSkillsGameBox - 1; i >= 0; i--) skillsGameBox[i] = -1;
		for(int i = Skills.maxSkills[kind] - 1; i >= 0; i--) skillsAllLevel[i] = -1;
		for(int i = GameScreen.aBodyPart - 1; i >= 0; i--) bodyIndex[i] = -1;
		for(int i = GameScreen.maxBagCells - 1; i >= 0; i--) bagItems[i] = null;
		aBagItems = 0;
		aWinFights = 0;
		aCombo4 = 0;
		aCombo5 = 0;
		aLoseFights = 0;
		aEarn5Stars = 0;
		causeDamage = 0;
		spendMoney = 0;
	}
	
	private int maxStatValue = 999;
	public int index_hud;
	
	private int kind;
	private int level;
	private int life;
	private int lifeMax;
	private int money;
	private int cash;
	private int healthMax;
	private int health;
	private int exp;
	private int power;
	private int tactic;
	private int brave;
	private int fire;
	private int water;
	private int air;
	private int earth;
	private int statFree;
	
	private int avatar;
	public String nick;
	
	public final static int HERO = 0;
	public final static int MONSTER = 1;
	public final static int aKind = 2;
	
	public int getKind() {
		return kind;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLifeMax() {
		return lifeMax;
	}

	public void setLifeMax(int lifeMax) {
		this.lifeMax = lifeMax;
	}
	
	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		if(this.money > money){
			spendMoney += this.money - money;
			game.runAchievementsDone(GameScreen.ACH_ID_SPEND_MONEY);
		}
		this.money = money;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}
	
	public int getHealthMax(boolean w_bonus) {
		int res = healthMax;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_ARMOR);
			if(od != null) res += od.health;
		}
		return res;
	}

	public void setHealthMax(int healthMax) {
		this.healthMax = healthMax;
		if(this.healthMax > maxStatValue) this.healthMax = maxStatValue;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		if(this.health > getHealthMax(true)) this.health = getHealthMax(true);
		if(this.health < 0) this.health = 0;
	}
	
	public void addHealth(int amount){
		setHealth(health + amount);
	}
	
	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public int getPower(boolean w_bonus) {
		int res = power;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_WEAPON);
			if(od != null) res += od.power;
		}
		return res;
	}

	public void setPower(int power) {
		this.power = power;
		if(this.power > maxStatValue) this.power = maxStatValue;
	}

	public int getTactic(boolean w_bonus) {
		int res = tactic;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_HAT);
			if(od != null) res += od.tactic;
		}
		return res;
	}

	public void setTactic(int tactic) {
		this.tactic = tactic;
		if(this.tactic > maxStatValue) this.tactic = maxStatValue;
	}

	public int getBrave(boolean w_bonus) {
		int res = brave;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_HAT);
			if(od != null) res += od.brave;
		}
		return res;
	}

	public void setBrave(int brave) {
		this.brave = brave;
		if(this.brave > maxStatValue) this.brave = maxStatValue;
	}

	public int getFire(boolean w_bonus) {
		int res = fire;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_OTHER);
			if(od != null) res += od.fire;
		}
		return res;
	}

	public void setFire(int fire) {
		this.fire = fire;
		if(this.fire > maxStatValue) this.fire = maxStatValue;
	}

	public int getWater(boolean w_bonus) {
		int res = water;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_OTHER);
			if(od != null) res += od.water;
		}
		return res;
	}

	public void setWater(int water) {
		this.water = water;
		if(this.water > maxStatValue) this.water = maxStatValue;
	}

	public int getAir(boolean w_bonus) {
		int res = air;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_OTHER);
			if(od != null) res += od.air;
		}
		return res;
	}

	public void setAir(int air) {
		this.air = air;
		if(this.air > maxStatValue) this.air = maxStatValue;
	}

	public int getEarth(boolean w_bonus) {
		int res = earth;
		if(w_bonus){
			OutfitData od = getBonusData(BODY_OTHER);
			if(od != null) res += od.earth;
		}
		return res;
	}

	public void setEarth(int earth) {
		this.earth = earth;
		if(this.earth > maxStatValue) this.earth = maxStatValue;
	}
	
	public int getStatFree() {
		return statFree;
	}

	public void setStatFree(int statFree) {
		this.statFree = statFree;
	}
	
	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public static final int MN_FIRE = 0;
	public static final int MN_WATER = 1;
	public static final int MN_AIR = 2;
	public static final int MN_EARTH = 3;
	public int aManna = 4;
	public int manna[] = new int[aManna];
	
	public void resetManna(){
		for(int i = aManna - 1; i >= 0; i--) manna[i] = 0;
	}
	
	private void startManna(){
		for(int i = aManna - 1; i >= 0; i--){
			manna[i] = getMaxManna(i) / 5;
		}
		for(int i = 0; i < skillStep.length; i++){
			skillStep[i] = 0;
		}
	}
	
	private int getMaxManna(int index){
		switch(index){
		case MN_FIRE:
			return fire;
		case MN_WATER:
			return water;
		case MN_AIR:
			return air;
		case MN_EARTH:
			return earth;
		default:
			return 0;
		}
	}
	
	public void setManna(int index, int value){
		manna[index] += value;
		int max = getMaxManna(index);
		if(manna[index] > max) manna[index] = max;
		if(manna[index] < 0) manna[index] = 0;
	}
	
	public int getManna(int index){
		return manna[index];
	}
	
	public final static int PERSI_POWER = 0;
	public final static int PERSI_TACTIC = 1;
	public final static int PERSI_BRAVE = 2;
	public final static int PERSI_FIRE = 3;
	public final static int PERSI_WATER = 4;
	public final static int PERSI_AIR = 5;
	public final static int PERSI_EARTH = 6;
	public final static int PERSI_EXP = 7;
	public final static int PERSI_LEVEL = 8;
	public final static int PERSI_HEALTH = 9;
	
	public int getStat(int index){
		switch(index){
		case PERSI_POWER:
			return getPower(true);
		case PERSI_TACTIC:
			return getTactic(true);
		case PERSI_BRAVE:
			return getBrave(true);
		case PERSI_FIRE:
			return getFire(true);
		case PERSI_WATER:
			return getWater(true);
		case PERSI_AIR:
			return getAir(true);
		case PERSI_EARTH:
			return getEarth(true);
		default:
			return 0;
		}
	}
	
	
	public float timerLife;
	public float oneLifeRegenTime;
	
	public void mathLife(float t_frame){
		if(life >= lifeMax) return;
		timerLife += t_frame;
		int dt = (int)(timerLife / oneLifeRegenTime);
		timerLife -= (float)dt * oneLifeRegenTime;
		setLife(getLife() + dt);
	}
	
	public String getLifeTime(){
		float t = oneLifeRegenTime - timerLife;
		int min = (int) (t / 60);
		String s_min = min < 10 ? "0"+min : ""+min;
		int sec = (int) (t - (min * 60));
		String s_sec = sec < 10 ? "0"+sec : ""+sec;
		return s_min+":"+s_sec;
	}
	
	public int hudHealthCurW;
	
	public void mathHudHealthW(int w_max){
		hudHealthCurW = health * w_max / healthMax;
	}
	
	public static final int maxSkillsGameBox = 3;
	public int skillsGameBox[] = new int[maxSkillsGameBox];
	
	public void addSkillGameBox(int i_skill){
		for(int i = 0; i < maxSkillsGameBox; i++){
			if(skillsGameBox[i] < 0){
				Skills.skill[kind][i_skill].gameBoxIndex = i;
				skillsGameBox[i] = i_skill;
				break;
			}
		}
	}
	
	public void delSkillGameBox(int i_skill){
		int i_skill_game_box = Skills.skill[kind][i_skill].gameBoxIndex;
		if(i_skill_game_box < 0) return;
		Skills.skill[kind][i_skill].gameBoxIndex = -1;
		skillsGameBox[i_skill_game_box] = -1;
	}
	
	public void delSkillGameBoxAll(){
		for(int i = maxSkillsGameBox - 1; i >= 0; i--){
			skillsGameBox[i] = -1;
		}
	}
	
	public int skillsAllLevel[] = new int[Skills.maxSkills[kind]];
	
	boolean isSkillCanActivate(int i_skill){
		return skillsAllLevel[i_skill] >= 0;
	}
	
	boolean isSkillAvailableToBuy(int i_skill){
		Skill sk = Skills.skill[kind][i_skill];
		if(level < sk.min_pers_level) return false;
		for(int i = 0; i < sk.a_demand; i++){
			if(sk.demand[i] == 0) continue;
			int id = sk.demand[i];
			int index = Skills.getSkillIndexByID(kind, id);
			if(skillsAllLevel[index] < 0) return false;
		}
		return true;
	}
	
	boolean isSkillCanUseInGame(int i_skill){
		int i_level = getSkillLevel(i_skill);
		if(i_level < 0) return false;
		int fire = Skills.getParam(kind, i_skill, i_level, Skill.SP_FIRE);
		if(this.getManna(MN_FIRE) < fire) return false;
		int water = Skills.getParam(kind, i_skill, i_level, Skill.SP_WATER);
		if(this.getManna(MN_WATER) < water) return false;
		int air = Skills.getParam(kind, i_skill, i_level, Skill.SP_AIR);
		if(this.getManna(MN_AIR) < air) return false;
		int earth = Skills.getParam(kind, i_skill, i_level, Skill.SP_EARTH);
		if(this.getManna(MN_EARTH) < earth) return false;
		return true;
	}
	
	int getSkillLevel(int i_skill){
		return skillsAllLevel[i_skill];
	}
	
	int aBagItems;
	int aOpenBagCells = 6;
	Outfit bagItems[] = new Outfit[GameScreen.maxBagCells];
	
	boolean buyBagItem(int i_outfit){
		int price = GameScreen.outfitData[i_outfit].price_buy;
		if(getMoney() < price) return false;
		if(aBagItems >= aOpenBagCells) return false;
		setMoney(getMoney() - price);
		bagItems[aBagItems++] = new Outfit(i_outfit, GameScreen.outfitData[i_outfit].strength);
		return true;
	}
	
	boolean repairBagItem(int i_bag){
		int price_repair = getBagItemRepairPrice(i_bag);
		if(getMoney() < price_repair) return false;
		setMoney(getMoney() - price_repair);
		
		Outfit o = bagItems[i_bag];
		OutfitData od = GameScreen.outfitData[o.index];
		o.setCurStrength(od.strength);
		return true;
	}
	
	public int getBagItemRepairPrice(int i_bag){
		Outfit o = bagItems[i_bag];
		OutfitData od = GameScreen.outfitData[o.index];
		int d_strength = o.getMaxStrength() - o.getCurStrength();
		return od.price_repair * d_strength;
	}
	
	boolean saleBagItem(int i_bag){
		int price_sale = getBagItemSalePrice(i_bag);
		setMoney(getMoney() + price_sale);
		for(int i = i_bag; i < aBagItems - 1; i++){
			bagItems[i] = bagItems[i+1];
		}
		aBagItems--;
		bagItems[aBagItems] = null;
		return true;
	}
	
	public int getBagItemSalePrice(int i_bag){
		Outfit o = bagItems[i_bag];
		OutfitData od = GameScreen.outfitData[o.index];
		return od.price_sale;
	}
	
	boolean setBagItem(int index, int i_outfit, int strength){
		if(aBagItems >= aOpenBagCells) return false;
		bagItems[index] = new Outfit(i_outfit, strength);
		aBagItems++;
		return true;
	}
	
	int bodyIndex[] = new int[GameScreen.aBodyPart];
	public static final int BODY_WEAPON = 0;
	public static final int BODY_ARMOR = 1;
	public static final int BODY_HAT = 2;
	public static final int BODY_OTHER = 3;
	
	void putOnBody(int i_bag){
		Outfit o = bagItems[i_bag];
		if(o == null) return;
		OutfitData od = GameScreen.outfitData[o.index];
		int index = od.id - 1;
		bodyIndex[index] = i_bag;
	}
	
	void takeOffBody(int i_bag){
		for(int i = GameScreen.aBodyPart - 1; i >= 0; i--){
			if(bodyIndex[i] == i_bag){
				bodyIndex[i] = -1;
			}
		}
	}
	
	private OutfitData getBonusData(int index){
		int i_bag = bodyIndex[index];
		if(i_bag < 0) return null;
		Outfit o = bagItems[i_bag];
		if(o.getCurStrength() == 0) return null;
		return GameScreen.outfitData[o.index];
	}
	
	public int skillUsingIndex;
	
	private int skillStep[];

	public int getSkillStep(int i_skill) {
		return skillStep[i_skill];
	}

	public void setSkillStep(int i_skill, int step) {
		this.skillStep[i_skill] = step;
	}
	
	//for skill that multiple x2 damage in game
	public boolean bProvocation;
	
	public boolean bAchievment[];
	
	//for achievements
	public int aWinFights;
	public int aCombo4;
	public int aCombo5;
	public int aLoseFights;
	public int aEarn5Stars;
	public int causeDamage;
	public int spendMoney;
}
