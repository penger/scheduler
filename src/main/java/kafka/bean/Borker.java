package kafka.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Borker {
	private int id;
	private String host;
	private int port;
	private int jmx_port;
	private String start_time;
	private String endpoints;

}
