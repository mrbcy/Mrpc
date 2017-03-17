package tech.mrbcy.mrpc.demo.demo3.domain;

public class Person {
	private int personId;
	private String personName;
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	@Override
	public String toString() {
		return "Person [personId=" + personId + ", personName=" + personName
				+ "]";
	}
	
	
}
