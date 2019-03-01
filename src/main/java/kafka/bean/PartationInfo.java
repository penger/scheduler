package kafka.bean;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PartationInfo {
	private int controllerEpoch;
	private int leaderEpoch;
	private int leaderID;
	private String isrs;
	private String group ;
	private String topic;
	private long offset;
	private long logSize;
	private long lag;
	private String owner;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
