/*
 * SubscriptionsDAO.java
 *
 * Created: Oct 7, 2009 7:16:00 PM
 *
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.urlgrey.mythpodcaster.dto.FeedSubscriptionItem;
import net.urlgrey.mythpodcaster.dto.FeedSubscriptions;

/**
 * @author scott
 *
 */
public interface SubscriptionsDAO {

	/**
	 * 
	 */
	List <FeedSubscriptionItem> findSubscriptions();

	void addSubscription(FeedSubscriptionItem item) throws IOException;

	/**
	 * @param seriesId
	 */
	void removeSubscription(String seriesId);

	/**
	 * @param purgeList
	 */
	void purge(List<FeedSubscriptionItem> purgeList);
	
}
