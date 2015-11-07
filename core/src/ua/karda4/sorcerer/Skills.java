package ua.karda4.sorcerer;

public class Skills {
	public final static int TEXT_TITLE = 0;
	public final static int TEXT_DESCRIPTION = 1;
	public final static int DEMAND_MIN_PERS_LEVEL = 3;
	
	static Skill skill[][] = new Skill[Pers.aKind][];
	static int maxSkills[] = new int[Pers.aKind];
	
	public static void createSkills(int kind, int size){
		maxSkills[kind] = size;
		skill[kind] = new Skill[size];
	}
	
	public static int getParam(int kind, int i_skill, int i_level, int param){
		return skill[kind][i_skill].getParam(i_level, param);
	}
	
	public static int getMaxLevel(int kind, int i_skill){
		return skill[kind][i_skill].maxLevel;
	}
	
	public static Skill getSkillByID(int kind, int id){
		for(int i = 0; i < maxSkills[kind]; i++){
			if(skill[kind][i].id == id) return skill[kind][i];
		}
		return null;
	}
	
	public static int getSkillIndexByID(int kind, int id){
		for(int i = 0; i < maxSkills[kind]; i++){
			if(skill[kind][i].id == id) return i;
		}
		return -1;
	}
}
