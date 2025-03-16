package com.wanderai.service;

import com.wanderai.dto.AiChatRequest;
import com.wanderai.dto.AiChatResponse;
import com.wanderai.dto.LocationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AiService {

    // In a real application, this would integrate with an AI service like OpenAI, Azure, etc.
    // For now, we'll simulate AI responses
    
    public AiChatResponse getChatResponse(AiChatRequest request) {
        String userMessage = request.getMessage();
        String aiResponse;
        List<LocationDto> suggestedLocations = new ArrayList<>();
        
        // Simple keyword-based response simulation
        if (userMessage.toLowerCase().contains("japan") || userMessage.toLowerCase().contains("日本")) {
            aiResponse = generateJapanResponse();
            suggestedLocations = getJapanLocations();
        } else if (userMessage.toLowerCase().contains("paris") || userMessage.toLowerCase().contains("france") || 
                   userMessage.toLowerCase().contains("巴黎") || userMessage.toLowerCase().contains("法国")) {
            aiResponse = generateParisResponse();
            suggestedLocations = getParisLocations();
        } else if (userMessage.toLowerCase().contains("new york") || userMessage.toLowerCase().contains("usa") || 
                   userMessage.toLowerCase().contains("纽约") || userMessage.toLowerCase().contains("美国")) {
            aiResponse = generateNewYorkResponse();
            suggestedLocations = getNewYorkLocations();
        } else {
            aiResponse = generateGenericResponse();
        }
        
        return AiChatResponse.builder()
                .message(aiResponse)
                .suggestedLocations(suggestedLocations)
                .build();
    }
    
    public AiChatResponse getLocationSuggestions(double lat, double lng) {
        // In a real app, you would use the coordinates to find nearby attractions
        // For now, we'll generate random suggestions based on the location
        
        String locationName = getLocationNameFromCoordinates(lat, lng);
        String aiResponse = "我注意到你对" + locationName + "附近感兴趣。这个地区有以下几个值得游览的景点：";
        
        List<LocationDto> suggestedLocations = new ArrayList<>();
        Random random = new Random();
        
        // Generate 3-5 random nearby locations
        int numLocations = random.nextInt(3) + 3;
        for (int i = 0; i < numLocations; i++) {
            // Generate random offsets (within ~5km)
            double latOffset = (random.nextDouble() - 0.5) * 0.1;
            double lngOffset = (random.nextDouble() - 0.5) * 0.1;
            
            LocationDto location = LocationDto.builder()
                    .name("景点 " + (i + 1))
                    .description("这是" + locationName + "附近的一个著名景点，值得一游。")
                    .lat(lat + latOffset)
                    .lng(lng + lngOffset)
                    .build();
            
            suggestedLocations.add(location);
            aiResponse += "\n\n" + (i + 1) + ". " + location.getName() + "：" + location.getDescription();
        }
        
        aiResponse += "\n\n你对这些景点有什么特别的兴趣吗？我可以提供更详细的信息。";
        
        return AiChatResponse.builder()
                .message(aiResponse)
                .suggestedLocations(suggestedLocations)
                .build();
    }
    
    public AiChatResponse getRoutePlanning(String destination) {
        String aiResponse = "我正在为你规划前往" + destination + "的路线。以下是我的建议：\n\n";
        
        // Simulate route planning response
        aiResponse += "1. 交通方式：\n";
        aiResponse += "   - 公共交通：可以乘坐地铁或公交车到达\n";
        aiResponse += "   - 自驾：全程约需30分钟，沿途有收费停车场\n";
        aiResponse += "   - 步行：如果距离不远，步行是欣赏沿途风景的好方式\n\n";
        
        aiResponse += "2. 最佳路线：\n";
        aiResponse += "   - 起点：当前位置\n";
        aiResponse += "   - 途经：主要道路和景点\n";
        aiResponse += "   - 终点：" + destination + "\n\n";
        
        aiResponse += "3. 时间建议：\n";
        aiResponse += "   - 预计行程时间：1-2小时\n";
        aiResponse += "   - 最佳出发时间：避开早晚高峰\n\n";
        
        aiResponse += "你有什么特别的偏好或需求吗？我可以根据你的需求调整路线规划。";
        
        // In a real app, you would generate actual route coordinates
        List<LocationDto> routePoints = new ArrayList<>();
        
        return AiChatResponse.builder()
                .message(aiResponse)
                .suggestedLocations(routePoints)
                .build();
    }
    
    private String getLocationNameFromCoordinates(double lat, double lng) {
        // In a real app, you would use a reverse geocoding service
        // For now, we'll return a generic name based on the coordinates
        return String.format("%.2f, %.2f", lat, lng);
    }
    
    private String generateJapanResponse() {
        return "日本是一个很棒的旅游目的地！以下是我的建议：\n\n" +
               "1. 东京：\n" +
               "   - 浅草寺\n" +
               "   - 东京塔\n" +
               "   - 涩谷十字路口\n" +
               "   - 秋叶原电器街\n\n" +
               "2. 京都：\n" +
               "   - 伏见稻荷大社\n" +
               "   - 金阁寺\n" +
               "   - 清水寺\n" +
               "   - 岚山竹林\n\n" +
               "3. 大阪：\n" +
               "   - 大阪城\n" +
               "   - 道顿堀美食街\n" +
               "   - 环球影城\n\n" +
               "建议行程：东京(3天) → 京都(2天) → 大阪(2天)\n\n" +
               "最佳旅游季节：春季(3-5月)樱花季或秋季(9-11月)红叶季。\n\n" +
               "你对这个计划有什么想法吗？";
    }
    
    private List<LocationDto> getJapanLocations() {
        List<LocationDto> locations = new ArrayList<>();
        
        locations.add(LocationDto.builder()
                .name("东京 - 浅草寺")
                .description("东京最古老的寺庙之一，著名的雷门和仲见世商店街")
                .lat(35.7147)
                .lng(139.7967)
                .build());
                
        locations.add(LocationDto.builder()
                .name("京都 - 伏见稻荷大社")
                .description("著名的千本鸟居，是京都最受欢迎的景点之一")
                .lat(34.9671)
                .lng(135.7727)
                .build());
                
        locations.add(LocationDto.builder()
                .name("大阪 - 大阪城")
                .description("日本著名的历史建筑，周围有美丽的公园")
                .lat(34.6873)
                .lng(135.5262)
                .build());
                
        return locations;
    }
    
    private String generateParisResponse() {
        return "巴黎是一个浪漫的城市，有很多值得游览的景点！以下是我的建议：\n\n" +
               "1. 必游景点：\n" +
               "   - 埃菲尔铁塔\n" +
               "   - 卢浮宫\n" +
               "   - 凯旋门\n" +
               "   - 巴黎圣母院\n\n" +
               "2. 特色体验：\n" +
               "   - 塞纳河游船\n" +
               "   - 蒙马特高地散步\n" +
               "   - 巴黎咖啡馆文化\n\n" +
               "3. 周边游览：\n" +
               "   - 凡尔赛宫\n" +
               "   - 枫丹白露宫\n\n" +
               "建议行程：市中心景点(3天) → 周边游览(1-2天)\n\n" +
               "最佳旅游季节：春季(4-6月)或秋季(9-10月)气候宜人。\n\n" +
               "你想了解更多关于巴黎的哪些方面？";
    }
    
    private List<LocationDto> getParisLocations() {
        List<LocationDto> locations = new ArrayList<>();
        
        locations.add(LocationDto.builder()
                .name("埃菲尔铁塔")
                .description("巴黎的标志性建筑，可以登顶俯瞰整个城市")
                .lat(48.8584)
                .lng(2.2945)
                .build());
                
        locations.add(LocationDto.builder()
                .name("卢浮宫")
                .description("世界上最大的艺术博物馆之一，收藏了蒙娜丽莎等名画")
                .lat(48.8606)
                .lng(2.3376)
                .build());
                
        locations.add(LocationDto.builder()
                .name("凯旋门")
                .description("位于香榭丽舍大街西端的标志性建筑")
                .lat(48.8738)
                .lng(2.2950)
                .build());
                
        return locations;
    }
    
    private String generateNewYorkResponse() {
        return "纽约是一个充满活力的大都市！以下是我的建议：\n\n" +
               "1. 曼哈顿景点：\n" +
               "   - 时代广场\n" +
               "   - 中央公园\n" +
               "   - 帝国大厦\n" +
               "   - 自由女神像\n\n" +
               "2. 博物馆和艺术：\n" +
               "   - 大都会艺术博物馆\n" +
               "   - 现代艺术博物馆(MoMA)\n" +
               "   - 自然历史博物馆\n\n" +
               "3. 特色体验：\n" +
               "   - 百老汇演出\n" +
               "   - 高线公园散步\n" +
               "   - 布鲁克林大桥步行\n\n" +
               "建议行程：曼哈顿(3-4天) → 布鲁克林(1天)\n\n" +
               "最佳旅游季节：春季(4-6月)或秋季(9-10月)气候宜人。\n\n" +
               "你对纽约的哪些方面特别感兴趣？";
    }
    
    private List<LocationDto> getNewYorkLocations() {
        List<LocationDto> locations = new ArrayList<>();
        
        locations.add(LocationDto.builder()
                .name("时代广场")
                .description("纽约的标志性地点，以霓虹灯广告和百老汇剧院闻名")
                .lat(40.7580)
                .lng(-73.9855)
                .build());
                
        locations.add(LocationDto.builder()
                .name("中央公园")
                .description("曼哈顿中心的大型城市公园，提供各种休闲活动")
                .lat(40.7829)
                .lng(-73.9654)
                .build());
                
        locations.add(LocationDto.builder()
                .name("自由女神像")
                .description("美国的标志性雕像，位于自由岛上")
                .lat(40.6892)
                .lng(-74.0445)
                .build());
                
        return locations;
    }
    
    private String generateGenericResponse() {
        return "感谢你的消息！我可以帮你规划旅行路线、推荐景点和提供旅行建议。请告诉我你想去的目的地，我会为你提供详细的旅游规划。\n\n" +
               "例如，你可以问我：\n" +
               "- 我想去日本旅游，有什么建议？\n" +
               "- 巴黎有哪些必去的景点？\n" +
               "- 纽约三日游怎么安排？\n\n" +
               "或者你可以在地图上选择一个地点，我会为你推荐附近的景点和活动。";
    }
}