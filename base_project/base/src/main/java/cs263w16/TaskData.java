package cs263w16;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class TaskData {
  String keyname;
  String value;
  Date date;

  //add constructors (default () and (String,String,Date))
  
    public TaskData() {
        super();
    }
  
  public TaskData(String keyname,String value,Date date)
  {
	  super();
	  this.keyname=keyname;
	  this.value=value;
	  this.date=date;
  }
  
  
  //add getters and setters for all fields
  
    public String getKeyname() {
        return keyname;
    }
    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }
    public String getValue() {
      return value;
    }
    public void setValue(String value) {
      this.value = value;
    }
    public Date getDate() {
      return date;
    }
    public void setDate(Date date) {
      this.date = date;
    }
  
} 
