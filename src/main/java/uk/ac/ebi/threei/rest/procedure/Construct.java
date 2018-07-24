package uk.ac.ebi.threei.rest.procedure;



public class Construct {

    
    private Long Id;
    private  String  Construct;

    public Construct(String construct) {
        Construct = construct;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getConstruct() {
        return Construct;
    }

    public void setConstruct(String construct) {
        Construct = construct;
    }

}
