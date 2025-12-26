package com.uixs.model.channel.dao;

import java.util.List;

import com.uixs.model.channel.dto.ChannelDTO;

public interface ChannelDAO {
	public List<ChannelDTO> channels();
	
	// 채널 insert
	public void insertChannel(ChannelDTO vo) throws Exception;
	
	// 채널 update
	public int updateChannel(ChannelDTO vo) throws Exception;
	
	// 채널 하나 select
	public ChannelDTO selectChannelOne(String id);
	
	public ChannelDTO selectCodeOne(String code);
	
	public int removeChannel(ChannelDTO dto) throws Exception;
}
