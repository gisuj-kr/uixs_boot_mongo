package com.uixs.service.channel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.channel.dao.ChannelDAO;
import com.uixs.model.channel.dto.ChannelDTO;

@Service
public class ChannelServiceImp implements ChannelService {
	
	@Autowired
	private ChannelDAO channelDao;
	
	public List<ChannelDTO> channels() {
		return channelDao.channels();
	};
	
	// 채널 insert
	public void insertChannel(ChannelDTO dto) throws Exception {
		channelDao.insertChannel(dto);
	};
	
	// 채널 update
	public int updateChannel(ChannelDTO dto) throws Exception {
		return channelDao.updateChannel(dto);
	};
	
	// 채널 하나 select
	public ChannelDTO selectChannelOne(String id) {
		return channelDao.selectChannelOne(id);
	};
	
	public ChannelDTO selectCodeOne(String code) {
		return channelDao.selectCodeOne(code);
	}
	
	public int removeChannel(ChannelDTO dto) throws Exception{
		return channelDao.removeChannel(dto);
	}
}
