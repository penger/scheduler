package kafka.bean;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 记录topic相关的信息
 * @author gongp
 *
 */
@Data
@Getter
@Setter
public class TopicMessage {
	private String topic;
	private int partionCount;
	private long maxMsg;
	private long currentMsg;
	private List<PartationInfo> partationInfos;

}
