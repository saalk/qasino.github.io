package cloud.qasino.games.utils;

/*
* class to create an object to hold all NPC information
*/

public class NPC_Object{
	private String race = null;
	private String height = null;
	private int weight = 0;
	private int age = 0;
	private String hairColor = null;
	private String hairStyle = null;
	private String eyeColor = null;
	private String job = null;
	private String sex = null;
	private String nationality = null;
	private String fName = "";
	private String lName = "";
	private String sName = "";
	private String cName = "";

  	public NPC_Object(String race, String height, int weight, int age, String hairColor, String hairStyle, String eyeColor, String job,
			String sex, String nationality, String fName, String lName, String sName, String cName) {
		this.race = race;
		this.height = height;
		this.weight = weight;
		this.age = age;
		this.hairStyle = hairStyle;
		this.hairColor = hairColor;
		this.eyeColor = eyeColor;
		this.job = job;
		this.sex = sex;
		this.nationality = nationality;
		this.fName = fName;
		this.lName = lName;
		this.sName = sName;
		this.cName = cName;
	}

	public NPC_Object() {

	}

	@Override
	public String toString() {
    	return "NPC_Object{" +
           "race='" + race + '\'' +
           ", height='" + height + '\'' +
           ", weight=" + weight +
           ", age=" + age +
		   ", hairStyle='" + hairStyle + '\'' +
           ", hairColor='" + hairColor + '\'' +
           ", eyeColor='" + eyeColor + '\'' +
           ", job='" + job + '\'' +
           ", sex='" + sex + '\'' +
           ", nationality='" + nationality + '\'' +
           ", fName='" + fName + '\'' +
           ", lName='" + lName + '\'' +
           ", sName='" + sName + '\'' +
           ", cName='" + cName + '\'' +
           '}';
}


	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getHairColor() {
		return hairColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	public String geteyeColor() {
		return eyeColor;
	}

	public void seteyeColor(String eyeColor) {
		this.eyeColor = eyeColor;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

}