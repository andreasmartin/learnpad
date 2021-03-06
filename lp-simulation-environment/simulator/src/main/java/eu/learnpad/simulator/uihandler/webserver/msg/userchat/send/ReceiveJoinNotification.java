/**
 *
 */
package eu.learnpad.simulator.uihandler.webserver.msg.userchat.send;

import eu.learnpad.simulator.uihandler.webserver.msg.userchat.IChatMsg;

/*
 * #%L
 * LearnPAd Simulator
 * %%
 * Copyright (C) 2014 - 2015 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

/**
 * @author Tom Jorquera - Linagora
 *
 */
public class ReceiveJoinNotification implements IChatMsg {

	public String channel;
	public String user;

	public ReceiveJoinNotification() {

	}

	public ReceiveJoinNotification(String channel, String user) {
		super();
		this.channel = channel;
		this.user = user;
	}

	@Override
	public TYPE getType() {
		return TYPE.NOTIF_JOIN;
	}

}
