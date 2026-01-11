package com.cinema.users.dto;

import com.cinema.users.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning premium upgrade response.
 * Used when a user upgrades to premium status.
 * Contains user information and upgrade details.
 *
 * @author Alexandru Tesula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPremiumUpgradeResponseDTO {

    private UserDTO user;
    private boolean isPremium;
    private String message;
    private double discountPercentage;
    private long appliedToBookingsCount;
}

