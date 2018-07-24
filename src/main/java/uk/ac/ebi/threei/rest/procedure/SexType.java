package uk.ac.ebi.threei.rest.procedure;

public enum SexType {

	female("female"),
	hermaphrodite("hermaphrodite"),
	male("male"),
	no_data("no data"),
	both("both");
	

	private final String name;

	SexType(String name) {
		this.name = name;
	}


	public String getName(){
		return this.toString();
	}
	public static SexType getByDisplayName(String displayName) {
		switch (displayName) {
			case "female":	return SexType.female;
			case "hermaphrodite": return SexType.hermaphrodite;
			case "male": return SexType.male;
			case "no data": return SexType.no_data;
			case "Male/Female": return SexType.both;
			default: throw new IllegalArgumentException("No enum constant " + SexType.class + "." + displayName);
		}
	}
}
