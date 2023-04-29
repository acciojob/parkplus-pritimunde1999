package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        User user;
        ParkingLot parkingLot;
        try {
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e)
        {
            throw new Exception("Cannot make reservation");
        }


        Reservation reservation = new Reservation();

        List<Spot> spotList = parkingLot.getSpotList();
        int min = Integer.MAX_VALUE;


        boolean checkSpot = false;
        for(Spot spot : spotList)
        {
            if(!spot.getOccupied())
            {
                checkSpot = true;
                break;
            }
        }

        if(!checkSpot)
        {
            throw new Exception("Cannot make reservation");
        }



        SpotType neededSpotType = null;
        if(numberOfWheels>4)
        {
            neededSpotType = SpotType.OTHERS;
        }
        else if(numberOfWheels>2)
        {
            neededSpotType = SpotType.FOUR_WHEELER;
        }
        else
        {
            neededSpotType = SpotType.TWO_WHEELER;
        }


       checkSpot = false;
       Spot minSpot = null;

        for(Spot spot : spotList)
        {
            if(!spot.getOccupied())
            {
                if(neededSpotType.equals(SpotType.OTHERS) && spot.getSpotType().equals(SpotType.OTHERS))
                {
                    int totalPrice = spot.getPricePerHour()*timeInHours;
                    if(totalPrice<min)
                    {
                        min = totalPrice;
                        minSpot = spot;
                        checkSpot = true;
                    }
                }

                else if(neededSpotType.equals(SpotType.FOUR_WHEELER) && (spot.getSpotType().equals(SpotType.OTHERS) ||
                        spot.getSpotType().equals(SpotType.FOUR_WHEELER)))
                {
                    int totalPrice = spot.getPricePerHour()*timeInHours;
                    if(totalPrice<min)
                    {
                        min = totalPrice;
                        minSpot = spot;
                        checkSpot = true;
                    }
                }

                else if(neededSpotType.equals(SpotType.TWO_WHEELER) && (spot.getSpotType().equals(SpotType.OTHERS)||
                        spot.getSpotType().equals(SpotType.FOUR_WHEELER) || spot.getSpotType().equals(SpotType.TWO_WHEELER)))
                {
                    int totalPrice = spot.getPricePerHour()*timeInHours;
                    if(totalPrice<min)
                    {
                        min = totalPrice;
                        minSpot = spot;
                        checkSpot = true;
                    }
                }
            }

        }

        minSpot.setOccupied(true);

        reservation.setSpot(minSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        minSpot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(minSpot);

        return reservation;

    }
}
