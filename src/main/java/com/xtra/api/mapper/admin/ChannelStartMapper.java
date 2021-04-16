package com.xtra.api.mapper.admin;

import com.xtra.api.model.AdvancedStreamOptions;
import com.xtra.api.model.Channel;
import com.xtra.api.projection.admin.channel.ChannelStart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Mapper(componentModel = "spring")
public abstract class ChannelStartMapper {


    @Mapping(source = "channel.streamInputs", target = "streamInputs")
//    @Mapping(source = "selectedSource", target = "selectedSource")
    public abstract ChannelStart convertToDto(Channel channel, int selectedSource);

    @AfterMapping
    void classifyAdvancedStreamOptions(Channel channel, @MappingTarget ChannelStart channelStart){
        AdvancedStreamOptions advancedStreamOptions = channel.getAdvancedStreamOptions();

        //input key values
        Map<String, String> inputKeyValues = new HashMap<>();
//        if (advancedStreamOptions.getHeaders() != null && !advancedStreamOptions.getHeaders().equals(""))
//            inputKeyValues.put("-headers", advancedStreamOptions.getHeaders());
//        if (advancedStreamOptions.getCookie() != null && !advancedStreamOptions.getCookie().equals(""))
//            inputKeyValues.put("-cookie", advancedStreamOptions.getCookie());

        if (!inputKeyValues.isEmpty())
            channelStart.setInputKeyValues(inputKeyValues);

        //output key values
        Map<String, String> outputKeyValues = new HashMap<>();
//        if (advancedStreamOptions.getHeaders() != null && !advancedStreamOptions.getHeaders().equals(""))
//            outputKeyValues.put("-headersOutput", advancedStreamOptions.getHeaders());
//        if (advancedStreamOptions.getCookie() != null && !advancedStreamOptions.getCookie().equals(""))
//            outputKeyValues.put("-cookieOutput", advancedStreamOptions.getCookie());

        if (!outputKeyValues.isEmpty())
        channelStart.setOutputKeyValues(outputKeyValues);

        //input flags
        Set<String> inputFlags = new HashSet<>();
//        if (advancedStreamOptions.getGeneratePts() != null && advancedStreamOptions.getGeneratePts())
//            inputFlags.add("-genpts");
//        if (advancedStreamOptions.getStreamAllCodecs() != null && advancedStreamOptions.getStreamAllCodecs())
//            inputFlags.add("-streamAll");

        if (!inputFlags.isEmpty())
            channelStart.setInputFlags(inputFlags);

        //output flags
        Set<String> outputFlags = new HashSet<>();
//        if (advancedStreamOptions.getAllowRecording()!= null && advancedStreamOptions.getAllowRecording())
//            outputFlags.add("-allowRecordingOutput");
//        if (advancedStreamOptions.getDirectSource()!= null && advancedStreamOptions.getDirectSource())
//            outputFlags.add("-directSourceOutput");

        if (!outputFlags.isEmpty())
            channelStart.setOutputFlags(outputFlags);

    }
}
