package com.marcosolina.wateringcan.services.implementations;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.marcosolina.wateringcan.services.interfaces.CronServices;

@Configuration
@EnableScheduling
public class CronServicesImpl implements CronServices {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronServicesImpl.class);

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${com.marcosolina.wateringcan.arduino.discovery.head-message}")
	private String headOfMessage;

	@Value("${com.marcosolina.wateringcan.arduino.discovery.port}")
	private int arduinoUdpPort;

	@Override
	@Scheduled(cron = "0 * * * * *")
	public void broadCastServerDiscoeryMessate() {
		try {
			List<InetAddress> list = listAllBroadcastAddresses();
			/*
			 * For some reason on my windows machine using a standard loop it did not work.
			 * On that Win Machine I have two network interfaces, and what I noticed is that
			 * is seems like that if SpringBoot is busy sending the UDP packets, he can not
			 * receive the Arduino reply... So I decided to use a parallel steam in order to
			 * send the UDP packets in a separate thread. This seems to workaround the issue
			 * so far
			 */
			// @formatter:off
			list.parallelStream().forEach(inetAddress -> {
				try {
					DatagramSocket socket = new DatagramSocket();

					socket.setBroadcast(true);

					String messageToSend = headOfMessage + serverPort + "X";

					byte[] buffer = messageToSend.getBytes();
					LOGGER.debug(String.format("Send UDP packet: %s on port: %d on networkd address: %s", messageToSend, arduinoUdpPort, inetAddress.toString()));
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, arduinoUdpPort);
					socket.send(packet);
					Thread.sleep(500);// give some time to the packet to be sent
					socket.close();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			});
			// @formatter:on
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}

	private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
		List<InetAddress> broadcastList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();

			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}

			networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
					.forEach(broadcastList::add);
		}
		return broadcastList;
	}

}
