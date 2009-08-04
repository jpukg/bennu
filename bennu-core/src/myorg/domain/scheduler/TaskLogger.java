/*
 * @(#)TaskLogger.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public class TaskLogger extends Thread {

    static private final int MAX_LOG_ENTRIES = 100;
    private final String taskId;
    private final Boolean successful;

    public TaskLogger(final Task task) {
	taskId = task.getExternalId();
	this.successful = null;
    }

    public TaskLogger(final Task task, final boolean successful) {
	taskId = task.getExternalId();
	this.successful = Boolean.valueOf(successful);
    }

    @Override
    public void run() {
	super.run();
	Transaction.withTransaction(false, new TransactionalCommand() {

	    @Override
	    public void doIt() {
		final Task task = AbstractDomainObject.fromExternalId(taskId);
		if (successful == null) {
		    task.createNewLog();
		} else {
		    task.updateLastLog(successful);
		    task.cleanupLogs(MAX_LOG_ENTRIES);
		}
	    }
	});
    }
}
