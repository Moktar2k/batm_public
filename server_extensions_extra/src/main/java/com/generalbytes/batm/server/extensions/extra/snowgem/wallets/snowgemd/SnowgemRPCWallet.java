package com.generalbytes.batm.server.extensions.extra.snowgem.wallets.snowgemd;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SnowgemRPCWallet implements IWallet {
    private static final Logger LOG = LoggerFactory.getLogger(SnowgemRPCWallet.class);
    private static final String CRYPTO_CURRENCY = CryptoCurrency.XSG.getCode();

    private BitcoinJSONRPCClient client;

    public SnowgemRPCWallet(String rpcURL) {
        this.client = createClient(rpcURL);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            LOG.error("snowgemd wallet error: unknown cryptocurrency {}.", cryptoCurrency);
            return null;
        }

        LOG.info("snowgemd sending coins from to: {} {}", destinationAddress, amount);
        try {
            String result = client.sendToAddress(destinationAddress, amount, description);
            LOG.debug("result = " + result);
            return result;
        } catch (BitcoinRPCException e) {
            LOG.error("Error", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            LOG.error("snowgemd wallet error: unknown cryptocurrency {}", cryptoCurrency);
            return null;
        }

        try {
            List<String> addressesByAccount = client.getAddressesByAccount("");
            if (addressesByAccount == null || addressesByAccount.size() == 0) {
                return null;
            } else {
                return addressesByAccount.get(0);
            }
        } catch (BitcoinRPCException e) {
            LOG.error("Error", e);
            return null;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            LOG.error("snowgemd wallet error: unknown cryptocurrency {}", cryptoCurrency);
            return null;
        }

        try {
            return client.getBalance();
        } catch (BitcoinRPCException e) {
            LOG.error("Error", e);
            return null;
        }
    }

    private static BitcoinJSONRPCClient createClient(String rpcURL) {
        try {
            return new BitcoinJSONRPCClient(rpcURL);
        } catch (MalformedURLException e) {
            LOG.error("Error", e);
        }
        return null;
    }
}
