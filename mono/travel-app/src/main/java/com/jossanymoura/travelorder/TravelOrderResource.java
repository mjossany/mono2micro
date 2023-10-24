package com.jossanymoura.travelorder;

import com.jossanymoura.flight.Flight;
import com.jossanymoura.flight.FlightResource;
import com.jossanymoura.hotel.Hotel;
import com.jossanymoura.hotel.HotelResource;
import com.jossanymoura.travelorder.TravelOrder;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("travelorder")
public class TravelOrderResource {

    @Inject
    FlightResource flightResource;

    @Inject
    HotelResource hotelResource;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TravelOrderDTO> orders() {
        return TravelOrder.<TravelOrder>listAll().stream()
                .map(
                        order -> TravelOrderDTO.of(
                                order,
                                flightResource.findByTravelOrderId(order.id),
                                hotelResource.findByTravelOrderId(order.id)
                        )
                ).collect(Collectors.toList());
    }

    @GET
    @Path("findById")
    public TravelOrder findById(@QueryParam("id") long id) {
        return TravelOrder.findById(id);
    }

    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TravelOrder newTravelOrder(TravelOrderDTO orderDTO) {
        TravelOrder order = new TravelOrder();
        order.id = null;
        order.persist();

        Flight flight = new Flight();
        flight.fromAirport = orderDTO.getFromAirport();
        flight.toAirport = orderDTO.getToAirport();
        flight.travelOrderId = order.id;
        flightResource.newFlight(flight);

        Hotel hotel = new Hotel();
        hotel.nights = orderDTO.getNights();
        hotel.travelOrderId = order.id;
        hotelResource.newHotel(hotel);

        return order;
    }
}
