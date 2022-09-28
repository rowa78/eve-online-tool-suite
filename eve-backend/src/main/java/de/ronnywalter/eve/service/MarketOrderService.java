package de.ronnywalter.eve.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import de.ronnywalter.eve.dto.queues.Queues;
import de.ronnywalter.eve.events.MarketOrderUpdateEvent;
import de.ronnywalter.eve.exception.EntityNotFoundException;
import de.ronnywalter.eve.model.Location;
import de.ronnywalter.eve.model.MarketOrder;
import de.ronnywalter.eve.model.Type;
import de.ronnywalter.eve.model.stats.PriceStats;
import de.ronnywalter.eve.repository.MarketOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketOrderService {

    private final MarketOrderRepository marketOrderRepository;
    private final ModelMapper modelMapper;

    @RabbitListener(queues = Queues.EVE_MARKETORDER_EVENTS)
    protected void onMarketOrderEvent(MarketOrderUpdateEvent evt) {
        int regionId = evt.getRegionId();

        if(evt.getMarketOrderDTOS() != null && evt.getMarketOrderDTOS().size() > 0) {
            log.info("processing " + evt.getMarketOrderDTOS().size() + " orders for region " + regionId + ", last modified: " + evt.getDate());
            List<Integer> ids = new ArrayList<>();
            List<MarketOrder> marketOrders = new ArrayList<>();
            List<MarketOrder> existingMarketOrders = marketOrderRepository.findByRegionId(evt.getRegionId());
            Map<Long, MarketOrder> existingMarketOrdersAsMap = new HashMap<>();
            Map<Long, MarketOrder> newMarketOrdersAsMap = new HashMap<>();

            Map<Long, MarketOrder> newOrders = new HashMap<>();
            Map<Long, MarketOrder> updatedOrders = new HashMap<>();
            Map<Long, MarketOrder> deletedOrders = new HashMap<>();

            evt.getMarketOrderDTOS().forEach(m -> newMarketOrdersAsMap.put(m.getOrderId(), map(m, MarketOrder.class)));
            existingMarketOrders.forEach(m -> existingMarketOrdersAsMap.put(m.getOrderId(), m));

            // find deleted orders
            existingMarketOrdersAsMap.keySet().forEach(k -> {
                if(!newMarketOrdersAsMap.containsKey(k)) {
                    deletedOrders.put(k, existingMarketOrdersAsMap.get(k));
                }
            });

            // find new orders
            newMarketOrdersAsMap.keySet().forEach(k -> {
                if(!existingMarketOrdersAsMap.containsKey(k)) {
                    newOrders.put(k, newMarketOrdersAsMap.get(k));
                }
            });

            // update existing orders
            newMarketOrdersAsMap.keySet().forEach(k -> {
                MarketOrder oldOrder = existingMarketOrdersAsMap.get(k);
                MarketOrder newOrder = newMarketOrdersAsMap.get(k);

                if(oldOrder != null && newOrder != null && !newOrder.equals(oldOrder)) {
                    oldOrder.updateValues(newOrder);
                    updatedOrders.put(k, oldOrder);
                }
            });

            log.info("Deleting orders: " + deletedOrders.size() + " in region " + regionId);
            deleteMarketOrders(Lists.newArrayList(deletedOrders.values()));
            log.info("Storing updated orders: " + updatedOrders.size()  + " in region " + regionId);
            saveMarketOrders(Lists.newArrayList(updatedOrders.values()));
            log.info("Storing new orders: " + newOrders.size() + " in region " + regionId);
            saveMarketOrders(Lists.newArrayList(newOrders.values()));
            log.info("Processing finished.");
        }
    }

    
    public MarketOrder saveMarketOrder(MarketOrder marketOrder) {
        return marketOrderRepository.save(marketOrder);
    }

    public MarketOrder getMarketOrder(long orderId) { return marketOrderRepository.findById(orderId).orElse(null);}


    @Transactional
    public List<MarketOrder> saveMarketOrders(List<MarketOrder> marketOrders) {
        return Lists.newArrayList(marketOrderRepository.saveAll(marketOrders));
    }

    public List<MarketOrder> getMarketOrdersForRegion(int regionId) {
        return marketOrderRepository.findByRegionId(regionId);
    }

    public List<MarketOrder> getMarketOrdersForRegionAndTypeId(int regionId, int typeId) {
        return marketOrderRepository.findByRegionIdAndTypeId(regionId, typeId);
    }

    public List<MarketOrder> getMarketOrdersForRegionAndTypeIdAndLocationId(Integer regionId, Integer typeId, Long locationId) {
        return marketOrderRepository.findByRegionIdAndTypeIdAndLocationId(regionId, typeId, locationId);
    }

    public List<MarketOrder> getMarketOrdersForLocation(long locationId) {
        return marketOrderRepository.findByLocationId(locationId);
    }

    public List<MarketOrder> getSellMarketOrdersForLocation(long locationId) {
        return marketOrderRepository.findByLocationIdAndIsBuyOrder(locationId, false);
    }

    public List<MarketOrder> getBuyMarketOrdersForLocation(long locationId) {
        return marketOrderRepository.findByLocationIdAndIsBuyOrder(locationId, true);
    }

    public List<Integer> getMarketOrderIdsForRegion(int region) {
        return marketOrderRepository.getMarketOrderIds(region);
    }

    public Multimap<Integer,MarketOrder> getSellMarketOrdersForLocationAsMap(long locationId) {
        Multimap<Integer, MarketOrder> orders = ArrayListMultimap.create();
        marketOrderRepository.findByLocationIdAndIsBuyOrder(locationId, false).forEach(m -> orders.put(m.getTypeId(), m));
        return orders;
    }

    public Multimap<Integer,MarketOrder> getBuyMarketOrdersForLocationAsMap(long locationId) {
        Multimap<Integer, MarketOrder> orders = ArrayListMultimap.create();
        marketOrderRepository.findByLocationIdAndIsBuyOrder(locationId, true).forEach(m -> orders.put(m.getTypeId(), m));
        return orders;
    }

    
    public void deleteMarketOrders(List<MarketOrder> orders) {
        marketOrderRepository.deleteAll(orders);
    }

    
    public void deleteMarketOrder(long orderId) {
        if(marketOrderRepository.existsById(orderId)) {
            marketOrderRepository.deleteById(orderId);
        } else {
            throw new EntityNotFoundException("Orderid " + orderId + " not found.");
        }
    }

    public Double getPrice(Type type, Location location) {
        return this.getPrice(type.getId(), location.getId());
    }

    public Double getPrice(int typeId, long locationId) {
        MarketOrder mo = marketOrderRepository.findFirstByLocationIdAndTypeIdAndIsBuyOrderOrderByPriceAsc(locationId, typeId, false);
        if(mo != null) {
            return mo.getPrice();
        } else {
            return -1d;
        }
    }

    public Double getBuyPrice(int typeId, long locationId) {
        MarketOrder mo = marketOrderRepository.findFirstByLocationIdAndTypeIdAndIsBuyOrderOrderByPriceDesc(locationId, typeId, true);
        if(mo != null) {
            return mo.getPrice();
        } else {
            return -1d;
        }
    }

    public Map<Integer, Double> getBuyPrices(List<Integer> types, int regionId, Long locationId) {
        List<PriceStats> stats = marketOrderRepository.queryBuyPrices(types, regionId);
        Map<Integer, Double> result = new HashMap<>();
        stats.forEach(ps -> result.put(ps.getTypeId(), ps.getPrice()));
        return result;
    }

    public Map<Integer, Double> getSellPrices(List<Integer> types, int regionId) {
        List<PriceStats> stats = marketOrderRepository.querySellPrices(types, regionId);
        Map<Integer, Double> result = new HashMap<>();
        stats.forEach(ps -> result.put(ps.getTypeId(), ps.getPrice()));
        return result;
    }

    public Map<Integer, Double> getSellPricesForRegionAndLocation(List<Integer> types, int regionId, Long locationId) {
        List<PriceStats> stats = marketOrderRepository.querySellPricesForRegionAndLocation(types, regionId, locationId);
        Map<Integer, Double> result = new HashMap<>();
        stats.forEach(ps -> result.put(ps.getTypeId(), ps.getPrice()));
        return result;
    }









    protected <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    protected <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

}
