package com.uixs.repository.channel;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.uixs.model.channel.dto.ChannelDTO;

public interface ChannelRepository extends MongoRepository<ChannelDTO, String>{

}
