
package cs263w16;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**  
 * @Project: base
 * @Title: TaskData.java
 * @Package cs263w16
 * @Description: TODO
 * @author YuesongWang
 * @date Jan 19, 2016 11:53:21 AM
 * @version V1.0  
 */
@XmlRootElement
public class TaskData {
    String keyname;
    String value;
    Date date;
    
    public TaskData() {
        super();
    }
    public TaskData(String keyname, String value, Date date) {
        super();
        this.keyname = keyname;
        this.value = value;
        this.date = date;
    }
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
