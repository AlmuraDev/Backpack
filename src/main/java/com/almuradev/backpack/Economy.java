/*
 * This file is part of Backpack, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.almuradev.backpack;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;
import java.util.Optional;

public class Economy {
    private Optional<EconomyService> service = Optional.empty();

    @Listener
    public void onServiceChange(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            service = Optional.of((EconomyService) event.getNewProviderRegistration().getProvider());
        }
    }

    public Optional<BigDecimal> getBalance(Player player) {
        if (service.isPresent()) {
            Optional<UniqueAccount> optAccount = service.get().getOrCreateAccount(player.getUniqueId());
            if (optAccount.isPresent()) {
                return Optional.of(optAccount.get().getBalance(service.get().getDefaultCurrency()));
            }
        }
        return Optional.empty();
    }

    public ResultType charge(Player player, BigDecimal amount) {
        if (service.isPresent()) {
            Optional<UniqueAccount> optAccount = service.get().getOrCreateAccount(player.getUniqueId());
            if (optAccount.isPresent()) {
                return optAccount.get().withdraw(service.get().getDefaultCurrency(), amount, Cause.of(NamedCause.source(Backpack.instance)))
                        .getResult();
            } else {
                return ResultType.FAILED;
            }
        } else {
            return ResultType.FAILED;
        }
    }
}
